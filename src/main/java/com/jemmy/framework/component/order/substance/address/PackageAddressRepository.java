package com.jemmy.framework.component.order.substance.address;

import com.jemmy.framework.component.user.User;
import com.jemmy.framework.controller.JpaRepository;

import java.util.List;

public interface PackageAddressRepository extends JpaRepository<PackageAddress> {

    List<PackageAddress> findAllByUser(User user);

}
