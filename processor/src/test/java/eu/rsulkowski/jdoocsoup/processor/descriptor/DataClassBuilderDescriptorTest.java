package eu.rsulkowski.jdoocsoup.processor.descriptor;

import org.junit.Assert;

import org.junit.Before;
import org.junit.Test;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import eu.rsulkowski.jdoocsoup.annotation.DataClassBuilder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by rsulkowski on 10/3/17.
 */
public class DataClassBuilderDescriptorTest {

    private ProcessingEnvironment mockedProcessingEnv;
    private TypeElement mockedTypeElement;
    private DataClassBuilderDescriptor dataClassBuilderDescriptor;
    private DataClassBuilder mockedDataClassBuilderAnnotation;
    private Elements mockedElementUtils;

    @Before
    public void before() {
        mockedDataClassBuilderAnnotation = mock(DataClassBuilder.class);
        mockedProcessingEnv = mock(ProcessingEnvironment.class);
        mockedTypeElement = mock(TypeElement.class);

        when(mockedDataClassBuilderAnnotation.name()).thenReturn("");
        when(mockedTypeElement.getAnnotation(DataClassBuilder.class)).thenReturn(mockedDataClassBuilderAnnotation);

        mockedElementUtils = mock(Elements.class);
        when(mockedProcessingEnv.getElementUtils()).thenReturn(mockedElementUtils);

        PackageElement mockedPackageElement = mock(PackageElement.class);
        when(mockedElementUtils.getPackageOf(any(Element.class))).thenReturn(mockedPackageElement);

        Name mockedQualifiedName = mock(Name.class);
        when(mockedPackageElement.getQualifiedName()).thenReturn(mockedQualifiedName);
        when(mockedQualifiedName.toString()).thenReturn("eu.rsulkowski.test");

        dataClassBuilderDescriptor = new DataClassBuilderDescriptor(mockedProcessingEnv, mockedTypeElement);
    }

    @Test
    public void testIfDescriptorWorks() {
        Assert.assertEquals("nullBuilder", dataClassBuilderDescriptor.getDataClassBuilderName());
    }
}
