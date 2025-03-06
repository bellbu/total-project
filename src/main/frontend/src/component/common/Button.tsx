import React from 'react';
import styled from "styled-components";
import {Colors} from "../../resource/Colors";

const Container = styled.div<{margin_top: string}>`
  width: 140px;
  height: 40px;
  border-radius: 24px;
  background-color: ${Colors.secondaryColor};
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  margin: ${props => props.margin_top};

  &:hover {
    opacity: 0.9;
  }
`;

const Text = styled.p`
  color: white;
  font-size: 21px;
  font-weight: 800;
`;

interface Props {
  label: string;
  onClick: () => void;
  marginTop?: string;
}

const Button = ({label, onClick, marginTop}: Props) => {
  return (
    <Container onClick={onClick} margin_top={marginTop ?? '13px 0 0 0'}>
      <Text>{label}</Text>
    </Container>
  );
};

export default Button;