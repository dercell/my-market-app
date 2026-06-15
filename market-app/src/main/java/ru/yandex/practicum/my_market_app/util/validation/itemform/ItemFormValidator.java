package ru.yandex.practicum.my_market_app.util.validation.itemform;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.my_market_app.model.dto.ItemForm;

import java.text.MessageFormat;
import java.util.List;

@Slf4j
public class ItemFormValidator implements ConstraintValidator<ItemFormValid, ItemForm> {

    @Override
    public boolean isValid(ItemForm form, ConstraintValidatorContext context) {
        boolean isValid = true;

        if (form.getId() == null) {
            context.buildConstraintViolationWithTemplate(
                    "Необходимо указать ID товара"
            ).addPropertyNode("id").addConstraintViolation();
            isValid = false;
        }

        if (!List.of("MINUS", "PLUS", "DELETE").contains(form.getAction())) {
            context.buildConstraintViolationWithTemplate(
                    MessageFormat.format("Действие {0} не поддерживается", form.getAction())
            ).addPropertyNode("action").addConstraintViolation();
            isValid = false;
        }

        return isValid;
    }
}
