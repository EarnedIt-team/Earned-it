package _team.earnedit.service.admin.item;

import _team.earnedit.dto.item.ItemRequest;
import _team.earnedit.entity.Item;
import _team.earnedit.global.ErrorCode;
import _team.earnedit.global.exception.item.ItemException;
import _team.earnedit.repository.ItemRepository;
import _team.earnedit.repository.PuzzleSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final PuzzleSlotRepository puzzleSlotRepository;

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
    @Transactional
    public void delete(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemException(ErrorCode.ITEM_NOT_FOUND,
                        String.format("삭제할 아이템이 없습니다. Id: %d", id)));

        // 퍼즐 슬롯 자체 삭제
        puzzleSlotRepository.deleteByItemId(id);

        // 아이템 삭제
        itemRepository.delete(item);
    }

    @Override
    @Transactional
    public void update(Long id, ItemRequest request) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemException(ErrorCode.ITEM_NOT_FOUND,
                        String.format("수정할 아이템이 존재하지 않습니다. Id: %d", id)));

        item.update(
                request.getName(),
                request.getVendor(),
                request.getPrice(),
                request.getImage(),
                request.getDescription(),
                request.getRarity(),
                request.getCategory()
        );
    }
}
