package spring.angular.social.service;
import java.io.IOException;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import spring.angular.social.entity.Profile;
import spring.angular.social.exception.ProfileNotFoundException;
import spring.angular.social.repository.ProfileRepository;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public Profile getProfile(Long profileId) {
        return profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found with ID: " + profileId));
    }

//    public Profile createProfile(Profile profile) {
//        return profileRepository.save(profile);
//    }
    
    public Profile createProfile(Profile profileJson, MultipartFile profileImage) {
        try {
            
            profileJson.setProfileImage(profileImage.getBytes());
            return profileRepository.save(profileJson);
        } catch (IOException e) {
            // Handle the exception appropriately
            return null;
        }
    }


    public Profile updateProfile(Long profileId, Profile updatedProfile) {
        Profile existingProfile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found with ID: " + profileId));

        existingProfile.setFullName(updatedProfile.getFullName());
        existingProfile.setBio(updatedProfile.getBio());

        return profileRepository.save(existingProfile);
    }

    public void deleteProfile(Long profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found with ID: " + profileId));

        profileRepository.delete(profile);
    }

    public Profile getProfileByUserId(Long userId) {
        // Assuming you have a repository for the Profile entity, e.g., ProfileRepository
        Optional<Profile> optionalProfile = profileRepository.findByUserId(userId);

        if (optionalProfile.isPresent()) {
            return optionalProfile.get();
        } else {
            throw new RuntimeException("Profile not found for user ID: " + userId);
        }
    }

}
