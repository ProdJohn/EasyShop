package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao
{
    public MySqlCategoryDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public List<Category> getAllCategories()
    {
        // get all categories
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories";
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                categories.add(mapRow(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);

        }
        return categories;
    }

    @Override
    public Category getById(int categoryId)
    {
        // get category by id
        String query = "SELECT * FROM categories WHERE category_id = ?";
        try (Connection connection = this.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, categoryId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRow(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }



    @Override
    public Category create(Category category)
    {
        // create a new category
        String query = "INSERT INTO categories (name, description) VALUES (?, ?)";

        try (Connection connection = this.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, category.getName());
            preparedStatement.setString(2, category.getDescription());


            int rows = preparedStatement.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Insert error, no rows affected.");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    category.setCategoryId(generatedId);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return category;
    }
    @Override
    public void update(int categoryId, Category category)
    {
        // update category
        String query = "UPDATE categories SET name = ?, description = ? WHERE category_id = ?";
        try (Connection connection = this.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, category.getName());
            preparedStatement.setString(2, category.getDescription());
            preparedStatement.setInt(3, categoryId);

            int rows = preparedStatement.executeUpdate();

            if (rows == 0) {
                throw new SQLException("Update error, no rows affected.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int categoryId)
    {
        // delete category
        String query = "DELETE FROM categories WHERE category_id = ?";
        try(Connection connection = this.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, categoryId);

            int rows = preparedStatement.executeUpdate();

            if (rows == 0) {
                throw new SQLException("Delete error, no rows affected.");
            }else{
                System.out.println("Category deleted successfully. "+ rows + " row(s) affected" );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private Category mapRow(ResultSet row) throws SQLException
    {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category()
        {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        return category;
    }

}


