package _team.earnedit.service.admin.item;

import _team.earnedit.entity.Item;

import java.util.List;

public interface ItemService {
    List<Item> findAll();
    Item findById(Long id);
    Item save(Item item);
    void delete(Long id);
}
