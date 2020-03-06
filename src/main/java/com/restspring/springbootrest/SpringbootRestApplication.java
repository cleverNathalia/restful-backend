package com.restspring.springbootrest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.List;

@SpringBootApplication
public class SpringbootRestApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootRestApplication.class, args);
	}

}

//PEOPLE CLASS WITH CONSTRUCTORS

@Entity
class People{
	private @Id @GeneratedValue Long id;
	private String name;
	private String address;
	private String phone;

	People(){}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	People(String name, String role){
		this.name = name;
		this.address = address;
		this.phone = phone;
	}
}

interface PeopleRepository extends JpaRepository<People, Long>{

}

//PEOPLE NOT FOUND HANDLER

class PeopleNotFoundException extends RuntimeException {

	PeopleNotFoundException(Long id) {
		super("Could not find employee " + id);
	}
}

@ControllerAdvice
class PeopleNotFoundAdvice {

	@ResponseBody
	@ExceptionHandler(PeopleNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	String peopleNotFoundHandler(PeopleNotFoundException ex) {
		return ex.getMessage();
	}
}


//PEOPLE CONTROLLER

@RestController
class PeopleController{


	private final PeopleRepository repository;

	PeopleController(PeopleRepository repository){
		this.repository = repository;
	}


	@CrossOrigin(origins = "http://localhost:8081")
	@GetMapping("/people/{role}")
	@ResponseBody
	People getPeople(@PathVariable String role){
		if (role.equals("admin")){
			People person = new People();
			person.setId(160712834959L);
			person.setName("Jane Johnson");
			person.setAddress("8 Brood Street, Cape Town");
			person.setPhone("0825943219");
			return person;

		}else{
			People person = new People();
			person.setId(920343204032L);
			person.setName("John Doe");
			person.setAddress("");
			person.setPhone("");
			return person;

		}
	}

	@GetMapping("/employees")
	List<People> all(){
		return repository.findAll();
	}

	@PostMapping("/employees")
	People newPeople(@RequestBody People newPeople){
		return repository.save(newPeople);
	}

	@GetMapping("/employees/{id}")
	People one(@PathVariable Long id){
		return repository.findById(id).orElseThrow(()-> new PeopleNotFoundException(id));
	}

	@PutMapping("/employees/{id}")
	People replacePeople(@RequestBody People newPeople, @PathVariable Long id){
		return repository.findById(id).map(people -> {
			people.setName(newPeople.getName());
			return repository.save(people);
		})
				.orElseGet(() -> {
					newPeople.setId(id);
					return repository.save(newPeople);
				});
	}

	@DeleteMapping("/employees/{id}")
	void deletePeople(@PathVariable Long id){
		repository.deleteById(id);
	}
}
