package campusconnect.backend.admin.user;

import campusconnect.backend.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {

	private Long id;
    private String name;
    private String email;
    private Role role;
    private boolean enabled;
}
