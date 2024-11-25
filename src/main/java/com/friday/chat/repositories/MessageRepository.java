package com.friday.chat.repositories;

import com.friday.chat.models.Group;
import com.friday.chat.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByGroup(Group group); // Find all messages for a given group
}
