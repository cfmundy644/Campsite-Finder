package com.PersonalProjects.CampgroundFinder;

import org.springframework.data.repository.CrudRepository;
import com.PersonalProjects.CampgroundFinder.Campsite;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface CampsiteRepository extends CrudRepository<Campsite, Integer> {

}