package com.amazonaws.saas.eks.cashdrawer.mapper;

import com.amazonaws.saas.eks.cashdrawer.dto.requests.workstations.CreateWorkstationRequest;
import com.amazonaws.saas.eks.cashdrawer.dto.requests.workstations.UpdateWorkstationRequest;
import com.amazonaws.saas.eks.cashdrawer.dto.responses.workstations.WorkstationResponse;
import com.amazonaws.saas.eks.cashdrawer.model.Workstation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface WorkstationMapper {
    WorkstationMapper INSTANCE = Mappers.getMapper(WorkstationMapper.class);

    Workstation createWorkstationRequestToWorkstation(CreateWorkstationRequest request);
    @Mapping(source = "workstation.partitionKey", target = "partitionKey")
    @Mapping(source = "workstation.id", target = "id")
    @Mapping(source = "workstation.number", target = "number")
    @Mapping(source = "workstation.created", target = "created")
    @Mapping(source = "workstation.modified", target = "modified")
    @Mapping(source = "request.name", target = "name")
    @Mapping(source = "request.ipAddress", target = "ipAddress")
    @Mapping(source = "request.hsn", target = "hsn")
    Workstation updateWorkstationRequestToWorkstation(UpdateWorkstationRequest request, Workstation workstation);
    WorkstationResponse workstationToWorkstationResponse(Workstation workstation);
    List<WorkstationResponse> workstationsToWorkstationResponse(List<Workstation> workstations);
}
