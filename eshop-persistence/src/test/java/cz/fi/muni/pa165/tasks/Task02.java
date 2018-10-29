package cz.fi.muni.pa165.tasks;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.validation.ConstraintViolationException;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import cz.fi.muni.pa165.PersistenceSampleApplicationContext;
import cz.fi.muni.pa165.entity.Category;
import cz.fi.muni.pa165.entity.Product;

 
@ContextConfiguration(classes = PersistenceSampleApplicationContext.class)
public class Task02 extends AbstractTestNGSpringContextTests {

	private Category electro;
	private Category kitchen;
	private Product flashlight;
	private Product robot;
	private Product plate;

	@PersistenceUnit
	private EntityManagerFactory emf;

	@BeforeClass
	private void setUp(){
		EntityManager entityManager = emf.createEntityManager();
		entityManager.getTransaction().begin();

		Category electro = new Category();
		electro.setName("Electro");
		Category kitchen = new Category();
		kitchen.setName("Kitchen");

		Product flash = new Product();
		flash.setName("Flashlight");
		Product robot = new Product();
		robot.setName("Kitchen robot");
		Product plate = new Product();
		plate.setName("Plate");

		flash.addCategory(electro);
		robot.addCategory(electro);
		robot.addCategory(kitchen);
		plate.addCategory(kitchen);

		entityManager.persist(electro);
		entityManager.persist(kitchen);
		entityManager.persist(flash);
		entityManager.persist(robot);
		entityManager.persist(plate);

		this.electro = electro;
		this.kitchen = kitchen;
		this.flashlight = flash;
		this.robot = robot;
		this.plate = plate;

		entityManager.getTransaction().commit();
		entityManager.close();
	}

	private void assertContainsCategoryWithName(Set<Category> categories,
			String expectedCategoryName) {
		for(Category cat: categories){
			if (cat.getName().equals(expectedCategoryName))
				return;
		}
			
		Assert.fail("Couldn't find category "+ expectedCategoryName+ " in collection "+categories);
	}
	private void assertContainsProductWithName(Set<Product> products,
			String expectedProductName) {
		
		for(Product prod: products){
			if (prod.getName().equals(expectedProductName))
				return;
		}
			
		Assert.fail("Couldn't find product "+ expectedProductName+ " in collection "+products);
	}

	@Test
	private void testElectro(){
		EntityManager em = emf.createEntityManager();
		Category retrievedCategory = em.find(Category.class,electro.getId());
		assertContainsProductWithName(retrievedCategory.getProducts(),"Flashlight");
		assertContainsProductWithName(retrievedCategory.getProducts(),"Kitchen robot");
		Assert.assertEquals(retrievedCategory.getProducts().size(),2);
		Assert.assertEquals(retrievedCategory.getName(),"Electro");
		em.close();
	}

	@Test
	private void testKitchen(){
		EntityManager em = emf.createEntityManager();
		Category retrievedCategory = em.find(Category.class,kitchen.getId());
		assertContainsProductWithName(retrievedCategory.getProducts(),"Plate");
		assertContainsProductWithName(retrievedCategory.getProducts(),"Kitchen robot");
		Assert.assertEquals(retrievedCategory.getProducts().size(),2);
		Assert.assertEquals(retrievedCategory.getName(),"Kitchen");
		em.close();
	}

	@Test
	private void testFlashlight(){
		EntityManager em = emf.createEntityManager();
		Product retrievedProduct = em.find(Product.class,flashlight.getId());
		assertContainsCategoryWithName(retrievedProduct.getCategories(),"Electro");
		Assert.assertEquals(retrievedProduct.getCategories().size(),1);
		Assert.assertEquals(retrievedProduct.getName(),"Flashlight");
		em.close();
	}

	@Test
	private void testRobot(){
		EntityManager em = emf.createEntityManager();
		Product retrievedProduct = em.find(Product.class,robot.getId());
		assertContainsCategoryWithName(retrievedProduct.getCategories(),"Electro");
		assertContainsCategoryWithName(retrievedProduct.getCategories(),"Kitchen");
		Assert.assertEquals(retrievedProduct.getCategories().size(),2);
		Assert.assertEquals(retrievedProduct.getName(),"Kitchen robot");
		em.close();
	}

	@Test
	private void testPlate(){
		EntityManager em = emf.createEntityManager();
		Product retrievedProduct = em.find(Product.class,plate.getId());
		assertContainsCategoryWithName(retrievedProduct.getCategories(),"Kitchen");
		Assert.assertEquals(retrievedProduct.getCategories().size(),1);
		Assert.assertEquals(retrievedProduct.getName(),"Plate");
		em.close();
	}

	@Test(expectedExceptions=ConstraintViolationException.class)
	public void testDoesntSaveNullName(){
		Product product = new Product();

		EntityManager entityManager = emf.createEntityManager();
		entityManager.getTransaction().begin();
		entityManager.persist(product);
		entityManager.getTransaction().commit();
		entityManager.close();
	}
	
}
