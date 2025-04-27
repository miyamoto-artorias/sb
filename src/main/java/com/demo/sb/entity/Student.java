package com.demo.sb.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true) //to ensure proper inheritance handling in equals and hashCode methods -> zadha claude
public class Student extends User {


}