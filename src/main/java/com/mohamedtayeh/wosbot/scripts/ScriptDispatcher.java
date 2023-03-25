package com.mohamedtayeh.wosbot.scripts;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.IFactory;

@Component
@RequiredArgsConstructor
@Command(name = "cli", mixinStandardHelpOptions = true)
public class ScriptDispatcher implements CommandLineRunner, ExitCodeGenerator {

  private final IFactory factory;
  private final ScriptParent scriptParent;
  private final ApplicationContext context;
  private int exitCode;

  @SuppressWarnings("RedundantThrows")
  @Override
  public void run(String... args) throws Exception {
    var cli = new CommandLine(scriptParent, factory);
    addSubCommands(cli);
    exitCode = cli.execute(args);
  }

  private void addSubCommands(CommandLine cli) {
    for (Script script : getSubCommands()) {
      cli.addSubcommand(script);
    }
  }

  private Collection<Script> getSubCommands() {
    return context.getBeansOfType(Script.class).values();
  }

  @Override
  public int getExitCode() {
    return exitCode;
  }
}
