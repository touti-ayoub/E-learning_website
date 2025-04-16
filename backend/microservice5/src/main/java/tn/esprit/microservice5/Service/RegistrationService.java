package tn.esprit.microservice5.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.microservice5.DTO.RegistrationDTO;
import tn.esprit.microservice5.Model.Event;
import tn.esprit.microservice5.Model.Registration;
import tn.esprit.microservice5.Model.RegistrationStatus;
import tn.esprit.microservice5.Model.User;
import tn.esprit.microservice5.Repo.IEventRepo;
import tn.esprit.microservice5.Repo.IRegistrationRepo;
import tn.esprit.microservice5.Repo.IUserRepo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RegistrationService {

    @Autowired
    private IRegistrationRepo registrationRepository;

    @Autowired
    private IUserRepo userRepository;

    @Autowired
    private IEventRepo eventRepository;

    public List<RegistrationDTO> getAllRegistrations() {
        return registrationRepository.findAll().stream()
                .map(RegistrationDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public RegistrationDTO getRegistrationById(long id) {
        Optional<Registration> registration = registrationRepository.findById(id);
        return registration.map(RegistrationDTO::fromEntity).orElse(null);
    }

    public List<RegistrationDTO> getRegistrationsByUserId(long userId) {
        return registrationRepository.findByUserUserId(userId).stream()
                .map(RegistrationDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<RegistrationDTO> getRegistrationsByEventId(long eventId) {
        return registrationRepository.findByEventEventId(eventId).stream()
                .map(RegistrationDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public RegistrationDTO createRegistration(long userId, long eventId) {
        Optional<User> userOptional = userRepository.findById(userId);
        Optional<Event> eventOptional = eventRepository.findById(eventId);

        if (userOptional.isPresent() && eventOptional.isPresent()) {
            User user = userOptional.get();
            Event event = eventOptional.get();

            // Check if user is already registered for this event
            if (registrationRepository.existsByUserUserIdAndEventEventId(userId, eventId)) {
                return null; // User already registered
            }

            // Check if event has reached max capacity
            if (event.getMaxCapacity() != null) {
                long currentRegistrations = registrationRepository.countByEventEventIdAndStatus(
                        eventId, RegistrationStatus.CONFIRMED);
                if (currentRegistrations >= event.getMaxCapacity()) {
                    return null; // Event is full
                }
            }

            Registration registration = new Registration();
            registration.setUser(user);
            registration.setEvent(event);
            registration.setStatus(RegistrationStatus.PENDING);
            registration.setRegistrationDate(LocalDateTime.now());
            registration.setPaymentStatus(false);

            Registration savedRegistration = registrationRepository.save(registration);
            return RegistrationDTO.fromEntity(savedRegistration);
        }
        return null;
    }

    public RegistrationDTO updateRegistrationStatus(long id, RegistrationStatus status) {
        Optional<Registration> registrationOptional = registrationRepository.findById(id);
        if (registrationOptional.isPresent()) {
            Registration registration = registrationOptional.get();
            registration.setStatus(status);
            Registration updatedRegistration = registrationRepository.save(registration);
            return RegistrationDTO.fromEntity(updatedRegistration);
        }
        return null;
    }

    public RegistrationDTO updatePaymentStatus(long id, boolean paymentStatus) {
        Optional<Registration> registrationOptional = registrationRepository.findById(id);
        if (registrationOptional.isPresent()) {
            Registration registration = registrationOptional.get();
            registration.setPaymentStatus(paymentStatus);

            // If payment is successful, update status to CONFIRMED
            if (paymentStatus) {
                registration.setStatus(RegistrationStatus.CONFIRMED);
            }

            Registration updatedRegistration = registrationRepository.save(registration);
            return RegistrationDTO.fromEntity(updatedRegistration);
        }
        return null;
    }

    public boolean deleteRegistration(long id) {
        if (registrationRepository.existsById(id)) {
            registrationRepository.deleteById(id);
            return true;
        }
        return false;
    }
}