package lms_system.service;

import lms_system.dto.GroupDto;
import lms_system.dto.PageResponse;
import lms_system.entity.Group;
import lms_system.mapper.GroupMapper;
import lms_system.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupService {
    private final GroupRepository groupRepository;
    private final GroupMapper groupMapper;

    @Transactional
    public GroupDto create(GroupDto dto) {
        Group group = groupMapper.toEntity(dto);
        return groupMapper.toDto(groupRepository.save(group));
    }

    public PageResponse<GroupDto> getAll(Pageable pageable) {
        Page<GroupDto> page = groupRepository.findAll(pageable).map(groupMapper::toDto);
        return new PageResponse<>(page.getContent(), page.getNumber(), page.getTotalPages(), page.getTotalElements());
    }

    public GroupDto getById(Long id) {
        return groupMapper.toDto(groupRepository.getOrThrow(id));
    }

    @Transactional
    public GroupDto update(Long id, GroupDto dto) {
        Group existing = groupRepository.getOrThrow(id);
        existing.setName(dto.getName());
        return groupMapper.toDto(groupRepository.save(existing));
    }

    @Transactional
    public void delete(Long id) {
        Group group = groupRepository.getOrThrow(id);
        group.setDeleted(true);
        groupRepository.save(group);
    }
}