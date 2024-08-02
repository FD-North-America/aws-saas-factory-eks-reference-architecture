package com.amazonaws.saas.eks.service;

import com.amazonaws.saas.eks.cashdrawer.dto.requests.workstations.CreateWorkstationRequest;
import com.amazonaws.saas.eks.cashdrawer.dto.requests.workstations.UpdateWorkstationRequest;
import com.amazonaws.saas.eks.cashdrawer.dto.responses.workstations.ListWorkstationsResponse;
import com.amazonaws.saas.eks.cashdrawer.dto.responses.workstations.WorkstationResponse;

public interface WorkstationService {
    /**
     * Creates a workstation
     * @param request {@link CreateWorkstationRequest
     * @param tenantId tenant id
     * @return {@link WorkstationResponse}
     */
    WorkstationResponse create(CreateWorkstationRequest request, String tenantId);

    /**
     * Gets a workstation
     * @param workstationId workstation id
     * @param tenantId tenant id
     * @return {@link WorkstationResponse}
     */
    WorkstationResponse get(String workstationId, String tenantId);

    /**
     * Updates a workstation
     * @param workstationId workstation id
     * @param request {@link UpdateWorkstationRequest}
     * @param tenantId tenant id
     * @return {@link WorkstationResponse}
     */
    WorkstationResponse update(String workstationId, UpdateWorkstationRequest request, String tenantId);

    /**
     * Deletes a workstation
     * @param workstationId workstation id
     * @param tenantId tenant id
     */
    void delete(String workstationId, String tenantId);

    /**
     * Returns all workstations
     * @param tenantId tenant id
     * @return {@link ListWorkstationsResponse}
     */
    ListWorkstationsResponse getAll(String tenantId);
}
