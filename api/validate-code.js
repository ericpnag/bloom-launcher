export default async function handler(req, res) {
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'POST, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type');
  if (req.method === 'OPTIONS') return res.status(200).end();

  try {
    const Stripe = (await import('stripe')).default;
    const stripe = new Stripe(process.env.STRIPE_SECRET_KEY);

    const { code } = req.body;
    if (!code) return res.status(400).json({ error: 'Missing code' });

    // Search recent payment intents for this code
    const payments = await stripe.paymentIntents.list({ limit: 100 });
    const match = payments.data.find(pi =>
      pi.status === 'succeeded' &&
      pi.metadata.redeem_code === code.trim().toUpperCase() &&
      !pi.metadata.redeemed
    );

    if (!match) {
      return res.json({ valid: false, error: 'Invalid or already redeemed code' });
    }

    // Mark as redeemed
    await stripe.paymentIntents.update(match.id, {
      metadata: { ...match.metadata, redeemed: 'true' }
    });

    res.json({
      valid: true,
      points: parseInt(match.metadata.totalPoints || '500'),
    });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
}
