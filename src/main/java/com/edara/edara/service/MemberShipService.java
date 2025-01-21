package com.edara.edara.service;

import com.edara.edara.model.dto.MemberShipRequest;
import com.edara.edara.model.dto.MemberShipResponse;
import com.edara.edara.model.entity.MemberShip;
import com.edara.edara.model.entity.Project;
import com.edara.edara.model.entity.Title;
import com.edara.edara.model.entity.User;
import com.edara.edara.model.enums.ProjectRole;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface MemberShipService extends CrudService<MemberShipRequest, MemberShip, MemberShipResponse, Long> {

    MemberShip add(User user, Project project, ProjectRole projectRole, Title title);
   //  void delete(User user, Project project);
    Optional<MemberShip> getEntityByUserIdAndProjectId(Long userId, Long projectId);
    MemberShip getByUserIdAndProjectId(Long userId, Long projectId);

    Optional<MemberShip> getEntityByUserCodeAndProjectId(String userCode, Long projectId);
    MemberShip getByUserCodeAndProjectId(String userCode, Long projectId);

}
