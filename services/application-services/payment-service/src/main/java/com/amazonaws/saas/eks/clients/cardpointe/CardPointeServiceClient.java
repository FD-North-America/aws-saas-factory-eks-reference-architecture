package com.amazonaws.saas.eks.clients.cardpointe;

import com.amazonaws.saas.eks.payment.clients.cardpointe.dto.requests.*;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@FeignClient(name = "cardpointe", url = "https://this-is-a-placeholder.com", configuration = CardPointeServiceClientConfig.class)
public interface CardPointeServiceClient {
    @PostMapping(value = "/v2/connect", produces = {MediaType.APPLICATION_JSON_VALUE})
    Response connect(@RequestBody ConnectRequest request);

    @PostMapping(value = "/v2/ping", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    ResponseEntity<Object> ping(@RequestBody PingRequest request);

    @PostMapping(value = "/v2/listTerminals", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    ResponseEntity<Object> listTerminals(@RequestBody ListTerminalsRequest request);

    @PostMapping(value = "/v2/disconnect", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    ResponseEntity<Object> disconnect(@RequestBody DisconnectRequest request);

    @PostMapping(value = "/v3/terminalDetails", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    ResponseEntity<Object> terminalDetails(@RequestBody TerminalDetailsRequest request);

    @PostMapping(value = "/v2/display", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    ResponseEntity<Object> display(@RequestBody DisplayRequest request);

    @PostMapping(value = "/v2/readInput", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    ResponseEntity<Object> readInput(@RequestBody ReadInputRequest request);

    @PostMapping(value = "/v2/readSignature", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    ResponseEntity<Object> readSignature(@RequestBody ReadSignatureRequest request);

    @PostMapping(value = "/v2/readConfirmation", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    ResponseEntity<Object> readConfirmation(@RequestBody ReadConfirmationRequest request);

    @PostMapping(value = "/v2/cancel", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    ResponseEntity<Object> cancel(@RequestBody CancelRequest request);

    @PostMapping(value = "/v2/readManual", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    ResponseEntity<Object> readManual(@RequestBody ReadManualRequest request);

    @PostMapping(value = "/v2/readCard", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    ResponseEntity<Object> readCard(@RequestBody ReadCardRequest request);

    @PostMapping(value = "/v3/authCard", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    ResponseEntity<Object> authCard(@RequestHeader("X-CardConnect-SessionKey") String sessionKey, @RequestBody AuthCardRequest request);

    @PostMapping(value = "/v3/authManual", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    ResponseEntity<Object> authManual(@RequestBody AuthManualRequest request);

    @PostMapping(value = "/v3/tip", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    ResponseEntity<Object> tip(@RequestBody TipRequest request);
}
