package com.microservice.repository;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class UserEntity
{
  @Id
  private long   id;
  @Column(name = "first_name")
  public  String firstName;
  @Column(name = "last_name")
  public  String lastName;
  @Column(name = "email")
  public  String email;

  public UserEntity()
  {
  }

  public UserEntity(String firstName, String email, String lastName)
  {
    this.firstName = firstName;
    this.lastName  = firstName;
    this.email     = email;
  }

  public String getFirstName()
  {
    return firstName;
  }

  public void setFirstName(String firstName)
  {
    this.firstName = firstName;
  }

  public String getEmail()
  {
    return email;
  }

  public void setEmail(String email)
  {
    this.email = email;
  }

  public long getId()
  {
    return id;
  }

  public void setId(long id)
  {
    this.id = id;
  }

  public String getLastName()
  {
    return lastName;
  }

  public void setLastName(String lastName)
  {
    this.lastName = lastName;
  }
}
