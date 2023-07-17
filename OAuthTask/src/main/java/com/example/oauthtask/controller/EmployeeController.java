package com.example.oauthtask.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.example.oauthtask.dto.EmployeeDTO;
import com.example.oauthtask.service.EmployeeService;



@RestController
@RequestMapping("/employees")
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;



	@PostMapping
	public ResponseEntity<EmployeeDTO> createEmployee(@RequestBody EmployeeDTO employeeDTO) {
		return new ResponseEntity<>(employeeService.addEmployee(employeeDTO), HttpStatus.CREATED);
	}

//	@PreAuthorize("hasAnyRole('ADMIN','USER')")
//	@GetMapping("/getAll")
//	public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
//
//		List<EmployeeDTO> employeeDTOs = employeeService.getAllEmployees();
//		return new ResponseEntity<>(employeeDTOs, HttpStatus.OK);
//	}
	@CrossOrigin(origins ="http://localhost:4200")
	@GetMapping("/getAll")
	public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    if (isAuthenticated(authentication)) {
	        if (hasAdminOrUserRole(authentication)) {
	            List<EmployeeDTO> employeeDTOs = employeeService.getAllEmployees();
	            return new ResponseEntity<>(employeeDTOs, HttpStatus.OK);
	        } else {
	            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	        }
	    } else {
	        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
	    }
	}

	 

	private boolean isAuthenticated(Authentication authentication) {
	    return authentication != null && authentication.isAuthenticated();
	}

	 

	private boolean hasAdminOrUserRole(Authentication authentication) {

        
        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userDetails.getAuthorities().stream()
                    .anyMatch(role -> role.getAuthority().equals("ADMIN") || role.getAuthority().equals("USER"));
        }
        return false;
	}



	@GetMapping("/getById/{id}")
	public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable("id") Long id) {
		EmployeeDTO employeeDTO = employeeService.getEmployeeById(id);
		if (employeeDTO != null) {
			return new ResponseEntity<>(employeeDTO, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

}
