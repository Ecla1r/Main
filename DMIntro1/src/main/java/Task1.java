import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.UserAuthResponse;
import com.vk.api.sdk.objects.groups.GroupFull;
import com.vk.api.sdk.queries.groups.GroupField;

import java.util.*;
import java.util.stream.Collectors;


public class Task1 {

    public static void main(String[] args) throws ClientException, ApiException {

        TransportClient transportClient = HttpTransportClient.getInstance();
        VkApiClient vk = new VkApiClient(transportClient);

        UserAuthResponse authResponse = vk.oauth()
                .userAuthorizationCodeFlow(2148288, "kavo", "", "code")
                .execute();

        UserActor actor = new UserActor(authResponse.getUserId(), authResponse.getAccessToken());

        List<String> publicIdList = vk.users()
                .getSubscriptionsExtended(actor)
                .count(5)
                .execute()
                .getItems()
                .stream()
                .map(jsonObject -> jsonObject.get("id").getAsString())
                .collect(Collectors.toList());


        List<String> myListOfThemes = vk.groups()
                .getById(actor)
                .groupIds(publicIdList)
                .fields(GroupField.ACTIVITY)
                .execute()
                .stream()
                .map(GroupFull::getActivity)
                .collect(Collectors.toList());

        List<Integer> myFriendsList = vk.friends()
                .get(actor)
                .execute()
                .getItems();

        List<Integer> finalFriends = myFriendsList.stream()
                .filter(integer -> {
                    try {
                        return compareThemes(myListOfThemes, friendsIdsToThemes(friendPublicToIds(integer, vk, actor), vk, actor));
                    } catch (ClientException e) {
                        e.printStackTrace();
                    } catch (ApiException e) {
                        e.printStackTrace();
                    }
                    return false;
                }).collect(Collectors.toList());

        System.out.println(finalFriends);
    }


    public static List<String> friendPublicToIds(Integer integer, VkApiClient vk, UserActor actor) throws ClientException, ApiException {
        return vk.groups()
                .get(actor)
                .userId(integer)
                .count(5)
                .execute()
                .getItems()
                .stream()
                .map(String::valueOf)
                .collect(Collectors.toList());
    }


    public static List<String> friendsIdsToThemes(List<String> myFriendsPublicIdList, VkApiClient vk, UserActor actor) throws ClientException, ApiException {
        return  vk.groups()
                .getById(actor)
                .groupIds(myFriendsPublicIdList)
                .fields(GroupField.ACTIVITY)
                .execute()
                .stream()
                .map(GroupFull::getActivity)
                .collect(Collectors.toList());
    }



    public static boolean compareThemes(List<String> myList, List<String> friendList) {
        double count = 0;
        Set<String> set1 = new HashSet<>(myList);
        Set<String> set2 = new HashSet<>(friendList);

        Iterator<String> it = set1.iterator();

        while (it.hasNext()) {
            String s = it.next();

            if (set2.contains(s)){
                count++;
            }
        }
        if(count / (double)set1.size() * 100.0 >= 60.0) {
            return true;
        }
        return false;
    }
}
