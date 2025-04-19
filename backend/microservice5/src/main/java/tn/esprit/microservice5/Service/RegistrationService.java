package tn.esprit.microservice5.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.microservice5.DTO.RegistrationDTO;
import tn.esprit.microservice5.Model.Event;
import tn.esprit.microservice5.Model.Registration;
import tn.esprit.microservice5.Model.User;
import tn.esprit.microservice5.Repo.IEventRepo;
import tn.esprit.microservice5.Repo.IRegistrationRepo;
import tn.esprit.microservice5.Repo.IUserRepo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RegistrationService {

    @Autowired
    private IRegistrationRepo registrationRepository;

    @Autowired
    private IEventRepo eventRepository;

    @Autowired
    private IUserRepo userRepository;

    public List<RegistrationDTO> getAllRegistrations() {
        return registrationRepository.findAll().stream()
                .map(RegistrationDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public RegistrationDTO getRegistrationById(long id) {
        Optional<Registration> registration = registrationRepository.findById(id);
        return registration.map(RegistrationDTO::fromEntity).orElse(null);
    }

    public RegistrationDTO createRegistration(RegistrationDTO registrationDTO) {
        Event event = eventRepository.findById(registrationDTO.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + registrationDTO.getEventId()));

        User user = userRepository.findById(registrationDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + registrationDTO.getUserId()));

        Registration registration = new Registration();
        registration.setEvent(event);
        registration.setUser(user);

        registration = registrationRepository.save(registration);
        return RegistrationDTO.fromEntity(registration);
    }

    public RegistrationDTO updateRegistration(long id, RegistrationDTO registrationDTO) {
        Optional<Registration> registrationOptional = registrationRepository.findById(id);
        if (registrationOptional.isPresent()) {
            Registration registration = registrationOptional.get();

            if (registrationDTO.getEventId() != null) {
                Event event = eventRepository.findById(registrationDTO.getEventId())
                        .orElseThrow(() -> new RuntimeException("Event not found with id: " + registrationDTO.getEventId()));
                registration.setEvent(event);
            }

            if (registrationDTO.getUserId() != null) {
                User user = userRepository.findById(registrationDTO.getUserId())
                        .orElseThrow(() -> new RuntimeException("User not found with id: " + registrationDTO.getUserId()));
                registration.setUser(user);
            }

            registration = registrationRepository.save(registration);
            return RegistrationDTO.fromEntity(registration);
        }
        return null;
    }

    public void deleteRegistration(long id) {
        registrationRepository.deleteById(id);
    }

    public List<RegistrationDTO> getRegistrationsByUserId(Long userId) {
        return registrationRepository.findByUser_UserId(userId).stream()
                .map(RegistrationDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<RegistrationDTO> getRegistrationsByEventId(Long eventId) {
        return registrationRepository.findByEvent_EventId(eventId).stream()
                .map(RegistrationDTO::fromEntity)
                .collect(Collectors.toList());
    }
}