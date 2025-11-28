package ru.hogwarts.school.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.repository.AvatarRepository;

@Service
public class AvatarService {
    private final AvatarRepository avatarRepository;

    public AvatarService(AvatarRepository avatarRepository) {
        this.avatarRepository = avatarRepository;
    }

    public Page<Avatar> getAllAvatars(Integer page, Integer size) {
        int validPage = (page == null || page < 0) ? 0 : page;
        int validSize = (size == null || size <= 0) ? 10 : Math.min(size, 100);

        Pageable pageable = PageRequest.of(page, size);
        return avatarRepository.findAll(pageable);
    }
}