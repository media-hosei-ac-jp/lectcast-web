package jp.ac.hosei.media.thymeleaf.dialect;

import java.util.HashSet;
import java.util.Set;
import jp.ac.hosei.media.thymeleaf.processor.BreakLineProcessor;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;

public class HoseiDialect extends AbstractProcessorDialect {

  private static final String NAME = "customized dialect";
  private static final String PREFIX = "hosei";

  public HoseiDialect() {
    super(NAME, PREFIX, StandardDialect.PROCESSOR_PRECEDENCE);
  }

  @Override
  public Set<IProcessor> getProcessors(String dialectPrefix) {
    Set<IProcessor> proccessors = new HashSet<>();

    proccessors.add(new BreakLineProcessor(dialectPrefix, getDialectProcessorPrecedence()));

    return proccessors;
  }
}
