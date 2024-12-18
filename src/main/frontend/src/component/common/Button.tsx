import React from 'react';
import styled from "styled-components";
import {Colors} from "../../resource/Colors";

const Container = styled.div<{margin_top: string}>`
  width: 143px;
  height: 40px;
  border-radius: 25px;
  background-color: ${Colors.secondaryColor};
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  margin-top: ${props => props.margin_top};
  
  &:hover {
    opacity: 0.9;
  }
`;

const Text = styled.p`
  color: white;
  font-size: 24px;
  font-weight: 800;
`;

interface Props {
  label: string;
  onClick: () => void;
  marginTop?: string;
}

const Button = ({label, onClick, marginTop}: Props) => {
  return (
    <Container onClick={onClick} margin_top={marginTop ?? '18px'}>
      <Text>{label}</Text>
    </Container>
  );
};

export default Button;