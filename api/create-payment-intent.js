export default async function handler(req, res) {
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'POST, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type');
  if (req.method === 'OPTIONS') return res.status(200).end();

  const TIERS = {
    '500':  { points: 500,  bonus: 0,    priceCents: 50, label: '500 Pulsar Points' },
    '1500': { points: 1500, bonus: 200,  priceCents: 50, label: '1500 Pulsar Points' },
    '3500': { points: 3500, bonus: 500,  priceCents: 50, label: '3500 Pulsar Points' },
    '8000': { points: 8000, bonus: 1500, priceCents: 50, label: '8000 Pulsar Points' },
  };

  try {
    const Stripe = (await import('stripe')).default;
    const stripe = new Stripe(process.env.STRIPE_SECRET_KEY);

    const { tier: tierKey } = req.body;
    const tier = TIERS[tierKey];
    if (!tier) return res.status(400).json({ error: 'Invalid tier' });

    const paymentIntent = await stripe.paymentIntents.create({
      amount: tier.priceCents,
      currency: 'usd',
      description: tier.label,
      metadata: {
        tier: tierKey,
        points: String(tier.points),
        bonus: String(tier.bonus),
        totalPoints: String(tier.points + tier.bonus),
      },
      automatic_payment_methods: { enabled: true },
    });

    res.json({
      clientSecret: paymentIntent.client_secret,
      publishableKey: process.env.STRIPE_PUBLISHABLE_KEY,
      tier: { points: tier.points, bonus: tier.bonus, total: tier.points + tier.bonus },
    });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
}
