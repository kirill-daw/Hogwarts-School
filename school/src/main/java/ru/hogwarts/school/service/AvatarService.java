package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.repository.AvatarRepository;

@Service
public class AvatarService {

    private static final Logger logger = LoggerFactory.getLogger(AvatarService.class);

    private final AvatarRepository avatarRepository;

    public AvatarService(AvatarRepository avatarRepository) {
        this.avatarRepository = avatarRepository;
        logger.debug("AvatarService initialized with repository");
    }

    public Page<Avatar> getAllAvatars(Integer page, Integer size) {
        logger.info("Was invoked method for get all avatars with pagination");
        logger.debug("Getting avatars with page={}, size={}", page, size);

        int validPage = (page == null || page < 0) ? 0 : page;
        int validSize = (size == null || size <= 0) ? 10 : Math.min(size, 100);

        logger.debug("Using page={}, size={} after validation", validPage, validSize);

        Pageable pageable = PageRequest.of(validPage, validSize);
        Page<Avatar> avatars = avatarRepository.findAll(pageable);

        logger.debug("Found {} avatars on page {} (total pages: {}, total elements: {})",
                avatars.getNumberOfElements(), validPage, avatars.getTotalPages(), avatars.getTotalElements());

        if (avatars.isEmpty()) {
            logger.warn("No avatars found in database");
        }

        return avatars;
    }
}