package iu.devinmehringer.project2.access.command;

import iu.devinmehringer.project2.model.command.CommandRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommandRepository extends JpaRepository<CommandRecord, Long> {
}
