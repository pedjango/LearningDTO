package net.pedjango.learningdto.dto;

import com.sun.istack.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ContactDTO {
    @NotNull
    private String email;

    private String phone;
}
