    package tn.esprit.microservice2.service;
    
    import com.stripe.Stripe;
    import com.stripe.exception.StripeException;
    import com.stripe.model.PaymentIntent;
    import com.stripe.model.PaymentMethod;
    import com.stripe.model.PaymentMethodCollection;
    import com.stripe.param.PaymentIntentCreateParams;
    import com.stripe.param.PaymentMethodListParams;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;
    import tn.esprit.microservice2.DTO.PaymentDTO;
    import tn.esprit.microservice2.DTO.PaymentIntentRequest;
    import tn.esprit.microservice2.DTO.PaymentIntentResponse;
    import tn.esprit.microservice2.Model.*;
    import tn.esprit.microservice2.repo.IPaymentRepository;
    import tn.esprit.microservice2.repo.IPaymentScheduleRepository;
    import tn.esprit.microservice2.repo.ISubscriptionRepository;
    
    import java.math.BigDecimal;
    import java.util.HashMap;
    import java.util.Map;
    
    @Service
    public class StripePaymentService {
        private static final Logger logger = LoggerFactory.getLogger(StripePaymentService.class);
    
        @Value("${stripe.api.key}")
        private String stripeApiKey;
    
        /*@Value("${stripe.webhook.secret}")
        private String webhookSecret;*/
    
        @Autowired
        private PaymentService paymentService;
    
        @Autowired
        private IPaymentRepository paymentRepository;
    
        @Autowired
        private ISubscriptionRepository subscriptionRepository;
    
        @Autowired
        private IPaymentScheduleRepository paymentScheduleRepository;
    
        /**
         * Creates a PaymentIntent in Stripe for a full payment
         */
        @Transactional
        public PaymentIntentResponse createPaymentIntent(PaymentIntentRequest request) throws StripeException {
            Stripe.apiKey = stripeApiKey;
    
            logger.info("Creating payment intent for amount {} {}", request.getAmount(), request.getCurrency());
    
            // Validate request
            if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Amount must be greater than zero");
            }
    
            if (request.getCurrency() == null || request.getCurrency().isEmpty()) {
                request.setCurrency("EUR"); // Default currency if not specified
            }
    
            // Convert to cents/smallest currency unit for Stripe
            long amountInSmallestUnit = request.getAmount().multiply(new BigDecimal(100)).longValue();
    
            // Create metadata to store application-specific information
            Map<String, String> metadata = new HashMap<>();
            if (request.getPaymentId() != null) {
                metadata.put("paymentId", request.getPaymentId().toString());
            }
            if (request.getSubscriptionId() != null) {
                metadata.put("subscriptionId", request.getSubscriptionId().toString());
            }
            if (request.getPaymentScheduleId() != null) {
                metadata.put("paymentScheduleId", request.getPaymentScheduleId().toString());
            }
    
            // Create payment intent with Stripe
            PaymentIntentCreateParams.Builder paramsBuilder = PaymentIntentCreateParams.builder()
                    .setAmount(amountInSmallestUnit)
                    .setCurrency(request.getCurrency().toLowerCase())
                    .setDescription("Payment for e-learning subscription")
                    .putAllMetadata(metadata) // Changed from setMetadata to putAllMetadata
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build()
                    );
    
            // If payment method ID is provided, attach it
            if (request.getPaymentMethod() != null && !request.getPaymentMethod().isEmpty()) {
                paramsBuilder.setPaymentMethod(request.getPaymentMethod());
                paramsBuilder.setConfirm(true);
                paramsBuilder.setReturnUrl("https://yourdomain.com/payment/success");
            }
    
            PaymentIntentCreateParams params = paramsBuilder.build();
            PaymentIntent intent = PaymentIntent.create(params);
    
            // Create response
            PaymentIntentResponse response = new PaymentIntentResponse();
            response.setClientSecret(intent.getClientSecret());
            response.setPaymentIntentId(intent.getId());
            response.setStatus(intent.getStatus());
            response.setPaymentId(request.getPaymentId());
            response.setSubscriptionId(request.getSubscriptionId());
            response.setPaymentScheduleId(request.getPaymentScheduleId());
    
            logger.info("Created payment intent: {}", intent.getId());
            return response;
        }
        /**
         * Confirms a payment intent and updates our system
         */
        @Transactional
        public Map<String, Object> confirmPayment(String paymentIntentId, Long paymentId, Long scheduleId) throws StripeException {
            Stripe.apiKey = stripeApiKey;
    
            logger.info("Confirming payment intent: {}", paymentIntentId);
    
            // Fetch PaymentIntent from Stripe
            PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);
    
            if (!"succeeded".equals(intent.getStatus())) {
                throw new IllegalStateException("Payment intent is not successful. Status: " + intent.getStatus());
            }
    
            // If the transaction is already successful in Stripe, now update our system
            if (scheduleId != null) {
                // This is an installment payment
                paymentService.processInstallmentPayment(scheduleId);
    
                // Get the updated schedule
                PaymentSchedule schedule = paymentScheduleRepository.findById(scheduleId).orElse(null);
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("status", "PAID");
                result.put("message", "Installment payment processed successfully");
                result.put("paymentIntentId", paymentIntentId);
                result.put("schedule", schedule);
                return result;
            } else if (paymentId != null) {
                // This is a full payment
                PaymentDTO updatedPayment = paymentService.updatePaymentStatus(paymentId);
    
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("status", "PAID");
                result.put("message", "Payment processed successfully");
                result.put("paymentIntentId", paymentIntentId);
                result.put("payment", updatedPayment);
                return result;
            } else {
                throw new IllegalArgumentException("Either paymentId or scheduleId must be provided");
            }
        }
    
        /**
         * Retrieves payment status from Stripe
         */
        public String getPaymentStatus(String paymentIntentId) throws StripeException {
            Stripe.apiKey = stripeApiKey;
            PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);
            return intent.getStatus();
        }
    
        /**
         * Lists saved payment methods for a customer
         */
        public PaymentMethodCollection listPaymentMethods(String customerId) throws StripeException {
            Stripe.apiKey = stripeApiKey;
    
            PaymentMethodListParams params = PaymentMethodListParams.builder()
                    .setCustomer(customerId)
                    .setType(PaymentMethodListParams.Type.CARD)
                    .build();
    
            return PaymentMethod.list(params);
        }
    
        /**
         * Handles a webhook event from Stripe
         * Note: Implementation should validate the signature and process different event types
         */
        public void handleWebhookEvent(String payload, String sigHeader) throws StripeException {
            // Webhook signature verification and event handling would be implemented here
            logger.info("Received Stripe webhook event");
            // Implementation details depend on what events you want to handle
        }
    }