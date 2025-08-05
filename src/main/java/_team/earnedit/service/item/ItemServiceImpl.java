package _team.earnedit.service.item;

import _team.earnedit.entity.Item;
import _team.earnedit.global.ErrorCode;
import _team.earnedit.global.exception.item.ItemException;
import _team.earnedit.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    @Override
    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    @Override
    public Item findById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ItemException(ErrorCode.ITEM_NOT_FOUND, String.format("아이템을 찾을 수 없습니다. Id: %d", id)));
    }

    @Override
    public Item save(Item item) {
        return itemRepository.save(item);
    }

    @Override
    public void delete(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new ItemException(ErrorCode.ITEM_NOT_FOUND, String.format("삭제할 아이템이 존재하지 않습니다. Id: %d", id));
        }
        itemRepository.deleteById(id);
    }
}
