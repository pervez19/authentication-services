package com.mycompany.authenticationservices.controller;


import com.mycompany.authenticationservices.dto.CompanyDTO;
import com.mycompany.authenticationservices.dto.Response;
import com.mycompany.authenticationservices.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * This controller is to provide all the company relevant api's
 *
 * @author pervez
 * @version 1.0
 * @since 01 January 2023
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
public class CompanyController {
    private final CompanyService companyService;

    /**
     * Find company list
     *
     * @return responseEntity - response entity with status code and data
     * @author pervez
     * @since 01 January 2023
     */
    @Operation(summary = "Get all the companies", description = "Get all the companies exist in DB")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "get company response", content = @Content(array = @ArraySchema(schema = @Schema(implementation = CompanyDTO.class)))),
            @ApiResponse(responseCode = "404, 500", description = "Not Found , Internal Server Error", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Response.class))))})
    @GetMapping
    public ResponseEntity<?> getCompanies() {
        log.info("getting all companies data from DB ");
        return ResponseEntity.ok(new Response("success", "Company List Retrieved Successfully", companyService.getAll()));
    }


    /**
     * Create company
     *
     * @param companyDTO - companyDTO object
     * @return responseEntity - response entity with status code and data
     * @author pervez
     * @since 01 January 2023
     */
    @Operation(summary = "Create company", description = "Create New company into DB")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "company data store into database", content = @Content(array = @ArraySchema(schema = @Schema(implementation = CompanyDTO.class)))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Response.class))))})
    @PostMapping
    public ResponseEntity<Response> createCompany(@RequestBody @Valid CompanyDTO companyDTO) {
        CompanyDTO createdCompanyDTO=companyService.save(companyDTO);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().path("/{accountId}")
                        .buildAndExpand(createdCompanyDTO.getAccountId()).toUri())
                .body(new Response("success", "Company Saved Successfully",createdCompanyDTO));
    }

    /**
     * Find Company by accountId
     *
     * @param accountId - Account id
     * @return responseEntity - response entity with status code and data
     * @author pervez
     * @since 01 January 2023
     */
    @Operation(summary = "Find company by account id", description = "Find company by account id")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Company Found", content = @Content(array = @ArraySchema(schema = @Schema(implementation = CompanyDTO.class)))),
            @ApiResponse(responseCode = "404, 500", description = "Company not found, Internal Server Error", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Response.class))))})
    @GetMapping("/account/{accountId}")
    public ResponseEntity<?> getCompanyByAccountId(@PathVariable String accountId) {
        return ResponseEntity.ok(new Response("success", "Company Loaded Successfully !", companyService.getByAccountId(accountId)));
    }


    /**
     * Update Company
     *
     * @param companyDTO - Company request
     * @param accountId      - Account id
     * @return responseEntity - response entity with status code and data
     * @author pervez
     * @since 01 January 2023
     */
    @Operation(summary = "Update company", description = "Update existing company into DB")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Company data update into database", content = @Content(array = @ArraySchema(schema = @Schema(implementation = CompanyDTO.class)))),
            @ApiResponse(responseCode = "404", description = "Company not found", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Response.class))))})
    @PutMapping("/account/{accountId}")
    public ResponseEntity<Response> updateCompanyByAccountId(@RequestBody CompanyDTO companyDTO, @PathVariable String accountId) {
        return ResponseEntity.ok(new Response("success", "Company Updated Successfully", companyService.updateByAccountId(accountId,companyDTO)));
    }

    /**
     * Delete company by id
     *
     * @param accountId - company id
     * @return responseEntity - response entity with status code and data
     * @author pervez
     * @since 01 January 2023
     */
    @Operation(summary = "Delete Company", description = "Delete existing Company into DB")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Company data Delete into database", content = @Content(array = @ArraySchema(schema = @Schema(implementation = CompanyDTO.class)))),
            @ApiResponse(responseCode = "404", description = "Company not found", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Response.class))))})
    @DeleteMapping("/{accountId}")
    public ResponseEntity<Response> deleteRole(@PathVariable("accountId") String accountId) {
        companyService.delete(accountId);
        return ResponseEntity.noContent().build();
    }
}
