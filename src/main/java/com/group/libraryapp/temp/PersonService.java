package com.group.libraryapp.temp;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PersonService {

    private final AddressRepository addressRepository;
    private final PersonRepository personRepository;

    public PersonService(AddressRepository addressRepository, PersonRepository personRepository) {
        this.addressRepository = addressRepository;
        this.personRepository = personRepository;
    }

    @Transactional
    public void savePerson() {
        Person person = personRepository.save(new Person()); // person 객체 생성 및 저장
        Address address = addressRepository.save(new Address()); // address 객체 생성 및 저장
        person.setAddress(address); // 두 객체를 연결시켜 줌: 생성된 person 객체의 address 필드를 업데이트 후 저장
        address.getPerson();
    }
}
