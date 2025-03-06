import React from 'react';
import styled from "styled-components";
import {Colors} from "../../resource/Colors";

const Container = styled.div<{width: string}>`
  width: ${props => props.width};
  height: 45px;
  border-radius: 25px;
  border: 2px solid ${Colors.secondaryColor};
  background-color: white;
  padding: 0 36px;
  
  box-sizing: border-box;
  
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
`;

const Title = styled.p<{fontSize?: string}>`
  font-size: ${props => props.fontSize ?? '17px'};
  font-weight: 700;
  color: black;
`;

const Input = styled.input`
  width: 50%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0);
  border: none;
`;

interface Props {
  title: string;
  value: string;
  onChange: (_: string) => void;
  width?: string;
  fontSize?: string;
}

const FormInput = ({ title, value, onChange, width, fontSize }: Props) => {
  return (
    <Container width={width ?? '27%'}>
      <Title fontSize={fontSize}>{title}</Title>
      <Input value={value} onChange={e => {
        onChange(e.target.value)
      }} />
    </Container>
  );
};

export default FormInput;