<?php
require 'vendor/autoload.php';

$price = 0;
if(isset($_POST['price'])){
  $price = (int)$_POST['price'];
}

$stripe = new \Stripe\StripeClient('sk_test_51Nuu6IJuz1SFvdOa5QruS1kFkf5PdiXXD5d0AKiafVcW01Ucjjr7MP15j3poHZCQUKWceQRfy0wvPegv6NSuIZJp003oMQp0Ri');

// Use an existing Customer ID if this is a returning customer.
$customer = $stripe->customers->create(
    [
        'name' => "Test",
        'address' => [
            'line1' => 'test line1',
            'postal_code' => 'test postal code',
            'city' => 'test city',
            'state' => 'test state',
            'country' => 'test country'
        ]
    ]
);
$ephemeralKey = $stripe->ephemeralKeys->create([
  'customer' => $customer->id,
], [
  'stripe_version' => '2022-08-01',
]);
$paymentIntent = $stripe->paymentIntents->create([
  'amount' => $price,
  'currency' => 'eur',
  'description' => 'Payment for Flight and/or Hotel Booking',
  'customer' => $customer->id,
  // In the latest version of the API, specifying the `automatic_payment_methods` parameter is optional because Stripe enables its functionality by default.
  'automatic_payment_methods' => [
    'enabled' => 'true',
  ],
]);

echo json_encode(
  [
    'paymentIntent' => $paymentIntent->client_secret,
    'ephemeralKey' => $ephemeralKey->secret,
    'customer' => $customer->id,
    'publishableKey' => 'pk_test_51Nuu6IJuz1SFvdOabOI3FsSuYoZtKSCgv6xQAd9lc8wkKmoFaYToDq1rW8noDAX2ODAiVWsUu0R4QENC4IDHLl6I00cbwY1CB3'
  ]
);
http_response_code(200);