package com.edara.edara.service.impl;

import com.edara.edara.exception.ConflictException;
import com.edara.edara.model.dto.MemberRequest;
import com.edara.edara.model.dto.MemberResponse;
import com.edara.edara.model.entity.Member;
import com.edara.edara.model.entity.Project;
import com.edara.edara.model.entity.Title;
import com.edara.edara.model.entity.User;
import com.edara.edara.model.enums.ProjectRole;
import com.edara.edara.model.mapper.MemberMapper;
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
    private final MemberMapper memberMapper;

    public Member toEntity(MemberRequest request) {
        return memberMapper.toEntity(request);
    }

    public MemberResponse toResponse(Member entity) {
        return memberMapper.toResponse(entity);
    }

    public Member create(MemberRequest memberRequest) {
        Member member = toEntity(memberRequest);
        return member;
    }
    public Member save(Member member) {
        return memberShipRepo.save(member);
    }

    public Member add(User user, Project project, ProjectRole projectRole, Title title) {

        Member newMember = new Member();

        newMember.setUser(user);
        newMember.setProject(project);
        newMember.setProjectRole(projectRole);
        newMember.setTitle(title);

        project.getMembers().add(newMember);

        return save(newMember);
    }

    @Override
    public Optional<Member> getEntityByUserIdAndProjectId(Long userId, Long projectId) {
        return memberShipRepo.findByUserIdAndProjectId(userId, projectId);
    }

    @Override
    public Member getByUserIdAndProjectId(Long userId, Long projectId) {
        return getEntityByUserIdAndProjectId(userId, projectId).orElseThrow(
                () -> new ConflictException("User with id = " + userId + " not involved in this project.")
        );
    }

    public Optional<Member> getEntityByUserCodeAndProjectId(String userCode, Long projectId) {
        return memberShipRepo.findByUser_UserCodeAndProject_Id(userCode, projectId);
    }

    @Override
    public Member getByUserCodeAndProjectId(String userCode, Long projectId) {
        return getEntityByUserCodeAndProjectId(userCode, projectId).orElseThrow(
                () -> new ConflictException("User with code = " + userCode + " not involved in this project.")
        );
    }


    @Override
    public Member updateEntity(Long aLong, Member newEntity) {
        return null;
    }

    @Override
    public MemberResponse update(Long aLong, MemberRequest memberRequest) {
        return null;
    }

    @Transactional
    @Override
    public void delete(Long membershipId) {
        Member member = getById(membershipId);

        member.getProject().getMembers().remove(member);
        member.getUser().getMembers().remove(member);

        //memberShipRepo.deleteById(membershipId); // no need for this because orphanRemoval = true in the relation
                                                   // Member and (Project and User) .
    }

    @Override
    public Optional<Member> getEntityById(Long membershipId) {
        return memberShipRepo.findById(membershipId);
    }

    @Override
    public Member getById(Long membershipId) {
        return getEntityById(membershipId).orElseThrow(
                () -> new NoSuchElementException("There is no membership with id  = " + membershipId)
        );
    }

    @Override
    public MemberResponse getResponseById(Long membershipId) {
        return toResponse(getById(membershipId));
    }

    @Override
    public List<MemberResponse> getAll() {
        return null;
    }
}
