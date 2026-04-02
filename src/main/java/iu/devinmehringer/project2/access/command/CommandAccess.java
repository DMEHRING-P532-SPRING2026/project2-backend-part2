package iu.devinmehringer.project2.access.command;

import iu.devinmehringer.project2.model.command.CommandRecord;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommandAccess {
    CommandRepository commandRepository;

    public CommandAccess(CommandRepository commandRepository) {
        this.commandRepository = commandRepository;
    }

    public void saveCommand(CommandRecord command) {
        commandRepository.save(command);
    }

    public List<CommandRecord> getCommands() {
        return commandRepository.findAll();
    }
}
