package jp.ac.hosei.media.thymeleaf.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.ITemplateEvent;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.processor.AbstractStandardExpressionAttributeTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;

public class BreakLineProcessor extends AbstractStandardExpressionAttributeTagProcessor {

  private static final String ATTR_NAME = "brtext";

  public BreakLineProcessor(String dialectPrefix, int precedence) {
    super(TemplateMode.HTML, dialectPrefix, ATTR_NAME, precedence, true);
  }

  @Override
  protected void doProcess(ITemplateContext context, IProcessableElementTag tag,
      AttributeName attributeName,
      String attributeValue, Object expressionResult,
      IElementTagStructureHandler structureHandler) {
    if (expressionResult == null) {
      return;
    }

    IModelFactory factory = context.getModelFactory();
    IModel model = factory.createModel();

    ITemplateEvent brTag = factory.createOpenElementTag("br");

    String[] lines = expressionResult.toString().split("\r\n|\r|\n", -1);
    for (String line : lines) {
      model.add(brTag);
      model.add(factory.createText(line));
    }
    model.remove(0);

    structureHandler.setBody(model, false);
  }
}
