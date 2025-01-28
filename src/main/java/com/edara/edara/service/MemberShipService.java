package com.edara.edara.service;

import com.edara.edara.model.dto.MemberRequest;
import com.edara.edara.model.dto.MemberResponse;
import com.edara.edara.model.entity.Member;
import com.edara.edara.model.entity.Project;
import com.edara.edara.model.entity.Title;
import com.edara.edara.model.entity.User;
import com.edara.edara.model.enums.ProjectRole;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface MemberShipService extends CrudService<MemberRequest, Member, MemberResponse, Long> {

    Member add(User user, Project project, ProjectRole projectRole, Title title);
   //  void delete(User user, Project project);
    Optional<Member> getEntityByUserIdAndProjectId(Long userId, Long projectId);
    Member getByUserIdAndProjectId(Long userId, Long projectId);

    Optional<Member> getEntityByUserCodeAndProjectId(String userCode, Long projectId);
    Member getByUserCodeAndProjectId(String userCode, Long projectId);

}
