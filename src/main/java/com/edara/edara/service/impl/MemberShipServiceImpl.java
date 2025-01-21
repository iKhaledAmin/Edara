package com.edara.edara.service.impl;

import com.edara.edara.exception.ConflictException;
import com.edara.edara.model.dto.MemberShipRequest;
import com.edara.edara.model.dto.MemberShipResponse;
import com.edara.edara.model.entity.MemberShip;
import com.edara.edara.model.entity.Project;
import com.edara.edara.model.entity.Title;
import com.edara.edara.model.entity.User;
import com.edara.edara.model.enums.ProjectRole;
import com.edara.edara.model.mapper.MemberShipMapper;
import com.edara.edara.repository.MemberShipRepo;
import com.edara.edara.service.MemberShipService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MemberShipServiceImpl implements MemberShipService {

    private final MemberShipRepo memberShipRepo;
    private final MemberShipMapper memberShipMapper;

    public MemberShip toEntity(MemberShipRequest request) {
        return memberShipMapper.toEntity(request);
    }

    public MemberShipResponse toResponse(MemberShip entity) {
        return memberShipMapper.toResponse(entity);
    }

    public MemberShip create(MemberShipRequest memberShipRequest) {
        MemberShip memberShip = toEntity(memberShipRequest);
        return memberShip;
    }
    public MemberShip save(MemberShip memberShip) {
        return memberShipRepo.save(memberShip);
    }

    public MemberShip add(User user, Project project, ProjectRole projectRole, Title title) {

        MemberShip newMemberShip = new MemberShip();

        newMemberShip.setUser(user);
        newMemberShip.setProject(project);
        newMemberShip.setProjectRole(projectRole);
        newMemberShip.setTitle(title);

        project.getMemberShips().add(newMemberShip);

        return save(newMemberShip);
    }

    @Override
    public Optional<MemberShip> getEntityByUserIdAndProjectId(Long userId, Long projectId) {
        return memberShipRepo.findByUserIdAndProjectId(userId, projectId);
    }

    @Override
    public MemberShip getByUserIdAndProjectId(Long userId, Long projectId) {
        return getEntityByUserIdAndProjectId(userId, projectId).orElseThrow(
                () -> new ConflictException("User with id = " + userId + " not involved in this project.")
        );
    }

    public Optional<MemberShip> getEntityByUserCodeAndProjectId(String userCode, Long projectId) {
        return memberShipRepo.findByUser_UserCodeAndProject_Id(userCode, projectId);
    }

    @Override
    public MemberShip getByUserCodeAndProjectId(String userCode, Long projectId) {
        return getEntityByUserCodeAndProjectId(userCode, projectId).orElseThrow(
                () -> new ConflictException("User with code = " + userCode + " not involved in this project.")
        );
    }


    @Override
    public MemberShip updateEntity(Long aLong, MemberShip newEntity) {
        return null;
    }

    @Override
    public MemberShipResponse update(Long aLong, MemberShipRequest memberShipRequest) {
        return null;
    }

    @Transactional
    @Override
    public void delete(Long membershipId) {
        MemberShip memberShip = getById(membershipId);

        memberShip.getProject().getMemberShips().remove(memberShip);
        memberShip.getUser().getMemberShips().remove(memberShip);

        //memberShipRepo.deleteById(membershipId); // no need for this because orphanRemoval = true in the relation
                                                   // MemberShip and (Project and User) .
    }

    @Override
    public Optional<MemberShip> getEntityById(Long membershipId) {
        return memberShipRepo.findById(membershipId);
    }

    @Override
    public MemberShip getById(Long membershipId) {
        return getEntityById(membershipId).orElseThrow(
                () -> new NoSuchElementException("There is no membership with id  = " + membershipId)
        );
    }

    @Override
    public MemberShipResponse getResponseById(Long membershipId) {
        return toResponse(getById(membershipId));
    }

    @Override
    public List<MemberShipResponse> getAll() {
        return null;
    }
}
