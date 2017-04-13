package com.vozer.dao;

import com.vozer.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by vqnguyen on 4/7/2017.
 */
@Repository
public class ImageDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final String insertImageSql = "INSERT\n" +
            "INTO\n" +
            "    `posts`(\n" +
            "        `USERID`,\n" +
            "        `story`,\n" +
            "        `tags`,\n" +
            "        `source`,\n" +
            "        `CID`,\n" +
            "        `pic`,\n" +
            "        `gif`,\n" +
            "        `mp4`,\n" +
            "        `webm`,\n" +
            "        `mediafile`,\n" +
            "        `youtube_key`,\n" +
            "        `fod_key`,\n" +
            "        `vfy_key`,\n" +
            "        `vmo_key`,\n" +
            "        `vine_key`,\n" +
            "        `fbv_key`,\n" +
            "        `url`,\n" +
            "        `time_added`,\n" +
            "        `phase_time`,\n" +
            "        `date_added`,\n" +
            "        `active`,\n" +
            "        `favclicks`,\n" +
            "        `pip`,\n" +
            "        `pip2`,\n" +
            "        `short`\n" +
            "    )\n" +
            "VALUES(\n" +
            "  1,?, ?, '',2, ?, 1, 1, 1, ?, '','','','','','',?, unix_timestamp(), unix_timestamp(), date(now()), 1, 1,'103.195.241.106', '', '' \n" +
            ")";

    public void insertImages(List<Post> images) {
        System.out.println("#### Import to DB ####");
        jdbcTemplate.batchUpdate(insertImageSql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Post image = images.get(i);
                ps.setString(1, image.getStory());
                ps.setString(2, image.getTags());
                ps.setString(3, image.getPic());
                ps.setString(4, image.getMediafile());
                ps.setString(5, image.getUrl());
            }

            @Override
            public int getBatchSize() {
                return images.size();
            }
        });
    }
}
