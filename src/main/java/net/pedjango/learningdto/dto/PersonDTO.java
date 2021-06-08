package net.pedjango.learningdto.dto;

import com.sun.istack.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PersonDTO {
    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    private List<ContactDTO> contacts;
}
