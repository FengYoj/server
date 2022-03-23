package com.jemmy.framework.component.message;

import com.jemmy.framework.controller.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

public interface MessageRepository extends JpaRepository<Message> {

    @Query(value = "select * from message where read_status = 0 limit 5", nativeQuery = true)
    List<Message> findAllByUnread();

    @Query("select m from Message m where m.readStatus = 0")
    Page<Message> findAllByUnread(Pageable pageable);

    Page<Message> findAllBySource(MessageSource source, Pageable pageable);

    @Query("select count(m) from Message m where m.readStatus = 0")
    Integer findUnreadCount();

    @Query("select count(m) from Message m")
    Integer findAllCount();

    @Query("select m.source as source, count(m) as count from Message m group by m.source")
    List<Map<String, Object>> findAllSource();

    @Transactional
    @Modifying
    @Query("update Message m set m.readStatus = ?1")
    void updateAllReadStatus(Integer readStatus);
}
