package hello;

import java.util.List;

import javax.inject.Inject;

import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.FriendList;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.facebook.api.Permission;
import org.springframework.social.facebook.api.Post;
import org.springframework.social.facebook.api.Reference;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class HelloController {

    private Facebook facebook;
	private ConnectionRepository connectionRepository;

    @Inject
    public HelloController(Facebook facebook, ConnectionRepository connectionRepository) {

        this.facebook = facebook;
		this.connectionRepository = connectionRepository;
    }

    @RequestMapping(method=RequestMethod.GET)
    public String helloFacebook(Model model) {
        if (connectionRepository.findPrimaryConnection(Facebook.class) == null) {
            return "redirect:/connect/facebook";
        }

        model.addAttribute("facebookProfile", facebook.userOperations().getUserProfile());
        PagedList<Post> feed = facebook.feedOperations().getFeed();
        model.addAttribute("feed", feed);
        model.addAttribute("albums", facebook.mediaOperations().getAlbums());
        System.out.println("앨범 : " + facebook.mediaOperations().getAlbums().size());
        System.out.println("친구목록 : " + facebook.friendOperations().getFriends().size());
        PagedList<Reference> friendList = facebook.friendOperations().getFriends();
        model.addAttribute("friendList", friendList);
        
        List<Permission> permission = facebook.userOperations().getUserPermissions();
        for ( int i=0; i<permission.size(); i++) {
        	System.out.println("Permission " + permission.get(i).getName() );
        }
        
        return "hello";
    }
    
	@RequestMapping(value="/message", method=RequestMethod.POST)
	public String postUpdate(String message) {
		facebook.feedOperations().updateStatus(message);
		return "redirect:/";
	}

}
