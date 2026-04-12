export default async function handler(req, res) {
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'POST, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type');
  if (req.method === 'OPTIONS') return res.status(200).end();

  try {
    const Stripe = (await import('stripe')).default;
    const stripe = new Stripe(process.env.STRIPE_SECRET_KEY);

    const { paymentIntentId } = req.body;
    if (!paymentIntentId) return res.status(400).json({ error: 'Missing paymentIntentId' });

    const pi = await stripe.paymentIntents.retrieve(paymentIntentId);
    if (pi.status !== 'succeeded') {
      return res.status(400).json({ error: 'Payment not completed' });
    }

    // Check if code was already generated for this payment
    if (pi.metadata.redeem_code) {
      return res.json({ code: pi.metadata.redeem_code, points: parseInt(pi.metadata.totalPoints) });
    }

    // Generate unique code: PULSAR-{points}-{random}
    const points = pi.metadata.totalPoints || '500';
    const rand = Math.random().toString(36).substring(2, 8).toUpperCase();
    const code = `PULSAR-${points}-${rand}`;

    // Store the code in the payment intent metadata so it can't be regenerated
    await stripe.paymentIntents.update(paymentIntentId, {
      metadata: { ...pi.metadata, redeem_code: code }
    });

    res.json({ code, points: parseInt(points) });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
}
