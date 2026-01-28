package org.example.kaoyanplatform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.kaoyanplatform.entity.FavoriteFolder;
import org.example.kaoyanplatform.mapper.FavoriteFolderMapper;
import org.example.kaoyanplatform.service.FavoriteFolderService;
import org.springframework.stereotype.Service;

@Service
public class FavoriteFolderServiceImpl extends ServiceImpl<FavoriteFolderMapper, FavoriteFolder> implements FavoriteFolderService {
}
