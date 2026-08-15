package lms_system.service;

import lms_system.dto.GroupDto;
import lms_system.entity.Group;
import lms_system.exception.ResourceNotFoundException;
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

    public Page<GroupDto> getAll(Pageable pageable) {
        return groupRepository.findAll(pageable).map(groupMapper::toDto);
    }

    public GroupDto getById(Long id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Группа с id " + id + " не найдена"));
        return groupMapper.toDto(group);
    }

    @Transactional
    public GroupDto update(Long id, GroupDto dto) {
        Group existing = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Группа с id " + id + " не найдена"));
        existing.setName(dto.getName());
        return groupMapper.toDto(groupRepository.save(existing));
    }

    @Transactional
    public void delete(Long id) {
        if (!groupRepository.existsById(id)) {
            throw new ResourceNotFoundException("Группа с id " + id + " не найдена");
        }
        groupRepository.deleteById(id);
    }
}