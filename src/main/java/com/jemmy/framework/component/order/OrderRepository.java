package com.jemmy.framework.component.order;

import com.jemmy.framework.component.user.User;
import com.jemmy.framework.controller.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Set;

@NoRepositoryBean
public interface OrderRepository<E extends Order> extends JpaRepository<E> {

    Page<E> findAllByUserAndStatusNotIn(User user, Pageable pageable, Set<Integer> status);

}
