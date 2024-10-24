package com.edara.edara.service;

import com.edara.edara.model.dto.MemberShipRequest;
import com.edara.edara.model.dto.MemberShipResponse;
import com.edara.edara.model.entity.MemberShip;
import org.springframework.stereotype.Service;

@Service
public interface MemberShipService extends CrudService<MemberShipRequest, MemberShip, MemberShipResponse, Long> {

     MemberShip toEntity(MemberShipRequest request);

     MemberShipResponse toResponse(MemberShip entity);

     MemberShip create(MemberShipRequest memberShipRequest);

     MemberShip save(MemberShip memberShip);

    MemberShip getByUserIdAndProjectId(Long userId, Long projectId);
}
