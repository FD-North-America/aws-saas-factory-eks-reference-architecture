package com.amazonaws.saas.eks.service.impl;

import com.amazonaws.saas.eks.cashdrawer.dto.requests.workstations.CreateWorkstationRequest;
import com.amazonaws.saas.eks.cashdrawer.dto.requests.workstations.UpdateWorkstationRequest;
import com.amazonaws.saas.eks.cashdrawer.dto.responses.workstations.ListWorkstationsResponse;
import com.amazonaws.saas.eks.cashdrawer.dto.responses.workstations.WorkstationResponse;
import com.amazonaws.saas.eks.cashdrawer.mapper.WorkstationMapper;
import com.amazonaws.saas.eks.cashdrawer.model.Workstation;
import com.amazonaws.saas.eks.exception.EntityNotFoundException;
import com.amazonaws.saas.eks.repository.WorkstationRepository;
import com.amazonaws.saas.eks.service.WorkstationService;
import org.springframework.stereotype.Service;

@Service
public class WorkstationServiceImpl implements WorkstationService {

    private static final String WORKSTATION_NOT_FOUND = "Workstation not found with ID: %s";

    private final WorkstationRepository repository;

    public WorkstationServiceImpl(WorkstationRepository repository) {
        this.repository = repository;
    }

    /**
     * Creates a workstation
     *
     * @param request  {@link CreateWorkstationRequest
     * @param tenantId tenant id
     * @param tenantId
     * @return {@link WorkstationResponse }
     */
    @Override
    public WorkstationResponse create(CreateWorkstationRequest request, String tenantId) {
        Workstation workstation = WorkstationMapper.INSTANCE.createWorkstationRequestToWorkstation(request);
        Workstation model = repository.save(tenantId, workstation);
        return WorkstationMapper.INSTANCE.workstationToWorkstationResponse(model);
    }

    /**
     * Gets a workstation
     *
     * @param workstationId workstation id
     * @param tenantId      tenant id
     * @return {@link WorkstationResponse}
     */
    @Override
    public WorkstationResponse get(String workstationId, String tenantId) {
        Workstation workstation = repository.get(tenantId, workstationId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(WORKSTATION_NOT_FOUND, workstationId)));
        return WorkstationMapper.INSTANCE.workstationToWorkstationResponse(workstation);
    }

    /**
     * Updates a workstation
     *
     * @param workstationId workstation id
     * @param request       {@link UpdateWorkstationRequest}
     * @param tenantId      tenant id
     * @return {@link WorkstationResponse}
     */
    @Override
    public WorkstationResponse update(String workstationId, UpdateWorkstationRequest request, String tenantId) {
        Workstation model = repository.get(tenantId, workstationId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(WORKSTATION_NOT_FOUND, workstationId)));
        Workstation workstation = WorkstationMapper.INSTANCE.updateWorkstationRequestToWorkstation(request, model);
        return WorkstationMapper.INSTANCE.workstationToWorkstationResponse(repository.save(tenantId, workstation));
    }

    /**
     * Deletes a workstation
     *
     * @param workstationId workstation id
     * @param tenantId      tenant id
     */
    @Override
    public void delete(String workstationId, String tenantId) {
        repository.delete(tenantId, workstationId);
    }

    /**
     * Returns all workstations
     *
     * @param tenantId tenant id
     * @return {@link ListWorkstationsResponse}
     */
    @Override
    public ListWorkstationsResponse getAll(String tenantId) {
        ListWorkstationsResponse response = new ListWorkstationsResponse();
        response.setWorkstations(WorkstationMapper.INSTANCE.workstationsToWorkstationResponse(repository.getAll(tenantId)));
        response.setCount(response.getWorkstations().size());
        return response;
    }
}
