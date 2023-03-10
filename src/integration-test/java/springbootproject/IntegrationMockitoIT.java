package springbootproject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;


@ExtendWith(MockitoExtension.class)
public class IntegrationMockitoIT {


    @Mock
    List<String> list;


    @Test
    @DisplayName("just one singe integration test")
    public void integration_testing(){

        Mockito.when(list.add(anyString())).thenReturn(true);

        list.add("something");

        Mockito.verify(list, times(1)).add(any(String.class));
    }


}
