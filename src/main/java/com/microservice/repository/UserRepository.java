package com.microservice.repository;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<UserEntity, Long>
{
  List<UserEntity> findById(int id);
}
