package com.edara.edara.service.impl;

import com.edara.edara.model.dto.MemberShipRequest;
import com.edara.edara.model.dto.MemberShipResponse;
import com.edara.edara.model.entity.MemberShip;
import com.edara.edara.model.mapper.MemberShipMapper;
import com.edara.edara.repository.MemberShipRepo;
import com.edara.edara.service.MemberShipService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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

    @Override
    public MemberShip getByUserIdAndProjectId(Long userId, Long projectId) {
        return memberShipRepo.findByUserIdAndProjectId(userId, projectId);
    }

    @Override
    public MemberShipResponse add(MemberShipRequest memberShipRequest) {
        return null;
    }

    @Override
    public MemberShip updateEntity(Long aLong, MemberShip newEntity) {
        return null;
    }

    @Override
    public MemberShipResponse update(Long aLong, MemberShipRequest memberShipRequest) {
        return null;
    }

    @Override
    public void delete(Long membershipId) {
        getById(membershipId);
        memberShipRepo.deleteById(membershipId);
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
