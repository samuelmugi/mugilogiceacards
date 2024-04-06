package com.mugi.logicea.uac.mapper;


import com.mugi.logicea.entiy.User;
import com.mugi.logicea.uac.dtos.SignUpRequest;
import com.mugi.logicea.uac.dtos.SignUpUpdateRequest;
import com.mugi.logicea.uac.dtos.UserResponse;
import com.mugi.logicea.utils.StringTrimmer;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
uses = StringTrimmer.class)
public interface UACMapper {
    UACMapper INSTANCE = Mappers.getMapper(UACMapper.class);

    User signUpToUserMapper(SignUpRequest signUpRequest);
    User signUpDateToUserMapper(SignUpUpdateRequest signUpUpdateRequest);
    UserResponse userEntotyToUserResponse(User user);

    List<UserResponse> userEntotyToUserResponseList(List<User> users);
}
