package application.api.controller;

import application.jpa.dto.UsuarioDTO;
import application.jpa.entities.Role;
import application.jpa.entities.Usuario;
import application.jpa.service.RoleService;
import application.jpa.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.HashSet;


@Controller
public class UsuarioController {

    static private final Logger LOGGER = LoggerFactory.getLogger(UsuarioController.class);

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RoleService roleService;

    @GetMapping(value = {"/", "/login"})
    public ModelAndView login() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        return modelAndView;
    }

    @GetMapping("/registration")
    public ModelAndView registration(){
        ModelAndView modelAndView = new ModelAndView();
        Usuario usuario = new Usuario();
        modelAndView.addObject("usuario", usuario);
        modelAndView.setViewName("registration");
        return modelAndView;
    }

    @PostMapping("/registration")
    public ModelAndView createNewUsuario(@Valid Usuario usuario, BindingResult bindingResult) {
        String modelView = "registration";
        return getRegistrationModelView(bindingResult, modelView, usuario);
    }

    @GetMapping("/admin/registration")
    public ModelAndView adminRegistration(){
        ModelAndView modelAndView = new ModelAndView();
        UsuarioDTO usuario = new UsuarioDTO();
        modelAndView.addObject("usuario", usuario);
        modelAndView.setViewName("admin/registration");
        return modelAndView;
    }

    @PostMapping("/admin/registration")
    public ModelAndView createNewAdminUsuario(@Valid UsuarioDTO usuarioDTO, BindingResult bindingResult){
        String modelView = "admin/registration";
        Usuario usuario = createUsuarioByDTO(usuarioDTO);

        return getRegistrationModelView(bindingResult, modelView, usuario);
    }

    private ModelAndView getRegistrationModelView(BindingResult bindingResult, String modelView, Usuario usuario) {
        ModelAndView modelAndView = new ModelAndView();
        Usuario usuarioExists = usuarioService.getUsuarioByUsername(usuario.getUsername());
        if (usuarioExists != null) {
            bindingResult
                    .rejectValue("username", "error.usuario",
                            "There is already a usuario registered with the usuario name provided");
        }
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName(modelView);
        } else {
            saveUser(modelView, usuario, modelAndView);
        }
        return modelAndView;
    }

    private void saveUser(String viewName, Usuario usuario, ModelAndView modelAndView){
        HashSet<Role> roles = roleService.getRoleByName(usuario.getRoles()==null?"USER":usuario.getFirstRole().getRole());

        usuarioService.saveUser(usuario, roles);
        modelAndView.addObject("successMessage", "Usuario has been registered successfully");
        modelAndView.addObject("usuario", new UsuarioDTO());
        modelAndView.setViewName(viewName);

    }

    private Usuario createUsuarioByDTO(UsuarioDTO usuarioDTO) {
        Usuario usuario = new Usuario();
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setUsername(usuarioDTO.getUsername());
        usuario.setPassword(usuarioDTO.getPassword());
        usuario.setRoles(roleService.getRoleByName(usuarioDTO.getRole()));
        return usuario;

    }

}
